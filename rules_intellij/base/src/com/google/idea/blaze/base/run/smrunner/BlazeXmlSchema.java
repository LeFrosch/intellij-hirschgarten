/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.run.smrunner;

import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/** Used to parse the test.xml generated by the blaze/bazel testing framework. */
public class BlazeXmlSchema {

  private static final JAXBContext CONTEXT;

  static {
    try {
      CONTEXT = JAXBContext.newInstance(TestSuite.class);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  static TestSuite parse(InputStream input) {
    try {
      return (TestSuite) CONTEXT.createUnmarshaller().unmarshal(input);
    } catch (JAXBException e) {
      throw new RuntimeException("Failed to parse test XML", e);
    }
  }

  @XmlRootElement(name = "testsuites")
  static class TestSuite {
    @XmlAttribute String name;
    @XmlAttribute String classname;
    @XmlAttribute int tests;
    @XmlAttribute int failures;
    @XmlAttribute int errors;
    @XmlAttribute int skipped;
    @XmlAttribute int disabled;
    @XmlAttribute double time;

    @XmlAttribute(name = "system-out")
    String sysOut;

    @XmlAttribute(name = "system-err")
    String sysErr;

    @XmlElement(name = "error", type = ErrorOrFailureOrSkipped.class)
    ErrorOrFailureOrSkipped error;

    @XmlElement(name = "failure", type = ErrorOrFailureOrSkipped.class)
    ErrorOrFailureOrSkipped failure;

    @XmlElement(name = "testsuite")
    List<TestSuite> testSuites = Lists.newArrayList();

    @XmlElement(name = "testdecorator")
    List<TestSuite> testDecorators = Lists.newArrayList();

    @XmlElement(name = "testcase")
    List<TestCase> testCases = Lists.newArrayList();
  }

  static class TestCase {
    @XmlAttribute String name;
    @XmlAttribute String classname;
    @XmlAttribute String status;
    @XmlAttribute String result;
    @XmlAttribute String time;

    @XmlAttribute(name = "system-out")
    String sysOut;

    @XmlAttribute(name = "system-err")
    String sysErr;

    @XmlElement(name = "error", type = ErrorOrFailureOrSkipped.class)
    ErrorOrFailureOrSkipped error;

    @XmlElement(name = "failure", type = ErrorOrFailureOrSkipped.class)
    ErrorOrFailureOrSkipped failure;

    @XmlElement(name = "skipped", type = ErrorOrFailureOrSkipped.class)
    ErrorOrFailureOrSkipped skipped;
  }

  static class ErrorOrFailureOrSkipped {
    @XmlValue String content;
    @XmlAttribute String message;
    @XmlAttribute String type;
  }
}
