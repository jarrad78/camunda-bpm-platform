/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.qa.upgrade.scenarios.eventsubprocess;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.qa.upgrade.DescribesScenario;
import org.camunda.bpm.qa.upgrade.ExtendsScenario;
import org.camunda.bpm.qa.upgrade.ScenarioSetup;
import org.camunda.bpm.qa.upgrade.Times;

/**
 * @author Thorben Lindhauer
 *
 */
public class NonInterruptingEventSubprocessScenario {

  @Deployment
  public static String deployProcess() {
    return "org/camunda/bpm/qa/upgrade/eventsubprocess/nonInterruptingMessageEventSubprocess.bpmn20.xml";
  }

  @DescribesScenario("init")
  @Times(4)
  public static ScenarioSetup instantiateAndTriggerSubprocess() {
    return new ScenarioSetup() {
      public void execute(ProcessEngine engine, String scenarioName) {
        engine
          .getRuntimeService()
          .startProcessInstanceByKey("NonInterruptingEventSubprocessScenario", scenarioName);

        engine.getRuntimeService()
          .createMessageCorrelation("Message")
          .processInstanceBusinessKey(scenarioName)
          .correlate();
      }
    };
  }

  @DescribesScenario("init.outerTask")
  @ExtendsScenario("init")
  @Times(3)
  public static ScenarioSetup completeSubprocessTask() {
    return new ScenarioSetup() {
      public void execute(ProcessEngine engine, String scenarioName) {
        Task task = engine
          .getTaskService()
          .createTaskQuery()
          .processInstanceBusinessKey(scenarioName)
          .taskDefinitionKey("outerTask")
          .singleResult();

        engine.getTaskService().complete(task.getId());
      }
    };
  }
}
