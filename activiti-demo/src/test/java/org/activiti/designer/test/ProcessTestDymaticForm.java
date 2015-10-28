package org.activiti.designer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Rule;
import org.junit.Test;

/**   
 * ��̬������ 
 * ������ֱ�ӷ��������ļ��У��������ݶ�����key��value����ʽ���ݱ������������
 * û�в��֣����еı�Ԫ�ض���˳�������ʾ��ҳ��(��activiti-explorer)
 * 
 * �����ƣ�ProcessTestDymaticForm   
 * ��������   
 * ����ʱ�䣺2015��10��28�� ����10:59:09   
 * �޸�ʱ�䣺2015��10��28�� ����10:59:09   
 * �޸ı�ע��   
 * @version       
 */ 
public class ProcessTestDymaticForm
{
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void startProcess() throws Exception
    {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        repositoryService.createDeployment()
                .addInputStream("DymaticForm.bpmn20.xml", ReflectUtil.getResourceAsStream("diagrams/DymaticForm.bpmn"))
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("DymaticForm").latestVersion().singleResult();

        // ͨ���������õ�����Key
        FormService formService = activitiRule.getFormService();
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        assertNull(startFormData.getFormKey());

        Map<String, String> formProperties = new HashMap<String, String>();
        formProperties.put("name", "HenryYan");

        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), formProperties);
        assertNotNull(processInstance);

        // ����ʱ����
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = runtimeService.getVariables(processInstance.getId());        
        assertEquals(variables.size(), 1);
        Set<Entry<String, Object>> entrySet = variables.entrySet();
        for (Entry<String, Object> entry : entrySet)
        {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        // ��ʷ��¼
        HistoryService historyService = activitiRule.getHistoryService();
        List<HistoricDetail> list = historyService.createHistoricDetailQuery().formProperties().list();
        assertEquals(1, list.size());

        // ��ȡ��һ���ڵ�
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        assertEquals("First Step", task.getName());

        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        assertNotNull(taskFormData);
        assertNull(taskFormData.getFormKey());
        List<FormProperty> taskFormProperties = taskFormData.getFormProperties();
        assertNotNull(taskFormProperties);
        for (FormProperty formProperty : taskFormProperties)
        {
            System.out.println(ToStringBuilder.reflectionToString(formProperty));
        }
        formProperties = new HashMap<String, String>();
        formProperties.put("setInFirstStep", "01/12/2015");
        formService.submitTaskFormData(task.getId(), formProperties);

        // ��ȡ�ڶ����ڵ�
        task = taskService.createTaskQuery().taskName("Second Step").singleResult();
        assertNotNull(task);
        taskFormData = formService.getTaskFormData(task.getId());
        assertNotNull(taskFormData);
        List<FormProperty> formProperties2 = taskFormData.getFormProperties();
        assertNotNull(formProperties2);
        assertEquals(1, formProperties2.size());
        assertNotNull(formProperties2.get(0).getValue());
        assertEquals(formProperties2.get(0).getValue(), "01/12/2015");
    }
}