#!/usr/bin/env python

import argparse
import collections
import json
import logging
import os

import opentuner
from opentuner import ConfigurationManipulator
from opentuner import IntegerParameter
from opentuner import LogIntegerParameter
from opentuner import MeasurementInterface
from opentuner import Result

from opentuner.search import manipulator

JVM_INT_PARAMS_FILE = 'jvm_int_params.json'
JVM_POWER_OF_TWO_PARAMS_FILE = 'jvm_power_of_two_params.json'


argparser = argparse.ArgumentParser(parents=opentuner.argparsers())
argparser.add_argument('source', help='source file to compile')
argparser.add_argument('--jar', dest='jar', type=bool, default=False, help='for jar')


class JVMTuner(MeasurementInterface):

  def __init__(self, *pargs, **kwargs):
    super(JVMTuner, self).__init__(program_name="programm_name", *pargs,
                                        **kwargs)

    self.int_params = json.load(open(JVM_INT_PARAMS_FILE))
    self.power_of_two_params = json.load(open(JVM_POWER_OF_TWO_PARAMS_FILE))


  def manipulator(self):
    m = ConfigurationManipulator()

    for param in self.int_params:
      param_value = self.int_params[param]

      if param_value['max'] > 128:
        m.add_parameter(LogIntegerParameter(
            param, param_value['min'], param_value['max']))
      else:
        m.add_parameter(IntegerParameter(
            param, param_value['min'], param_value['max']))

    for param in self.power_of_two_params:
      param_value = self.power_of_two_params[param]

      m.add_parameter(manipulator.PowerOfTwoParameter(
            param, param_value['min'], param_value['max']))

    return m


  def cfg_to_cmd(self, cfg, programm_name):
    java_cmd = 'java '
    if args.jar:
      java_cmd += '-jar '

    java_cmd += programm_name

    for param in self.int_params:
      java_cmd += ' -{0}{1}'.format(param, cfg[param])

    for param in self.power_of_two_params:
      java_cmd += ' -{0}{1}'.format(param, cfg[param])

    return java_cmd



  def run(self, desired_result, input, limit):
    """
    Compile and run a given configuration then
    return performance
    """
    cfg = desired_result.configuration.data

    jvm_cmd = self.cfg_to_cmd(cfg, args.source)

    run_result = self.call_program(jvm_cmd)
    assert run_result['returncode'] == 0

    return Result(time=run_result['time'])

  def save_final_config(self, configuration):
    """called at the end of tuning"""
    print "Optimal block size written to jvm_final_config.json:", configuration.data
    self.manipulator().save_to_file(configuration.data,
                                    'jvm_final_config.json')


if __name__ == '__main__':
  opentuner.init_logging()
  args = argparser.parse_args()
  JVMTuner.main(args)
