package com.yege.demo;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
    }
    @Test
    public void testCreateProcessEngine() {
    /* ProcessEngine创建方式：
        1 使用代码创建工作流需要的23张表*/
        System.out.println("1111111111111111111111111111");
            ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
            //连接数据库的配置
            processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
            processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/Activiti_demo?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
            processEngineConfiguration.setJdbcUsername("root");
            processEngineConfiguration.setJdbcPassword("123");
            /**
             public static final String DB_SCHEMA_UPDATE_FALSE = "false";不能自动创建表，需要表存在
             public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";先删除表再创建表
             public static final String DB_SCHEMA_UPDATE_TRUE = "true";如果表不存在，自动创建表
             */
            processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
            //工作流的核心对象，ProcessEnginee对象  创建工作流对象的时候把表创建
            ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
            System.out.println(processEngine);

            //注：在Activiti中，在创建核心的流程引擎对象时会自动建表。如果程序正常执行，mysql会自动建库，然后创建23张表。
        //会默认加载resources下的 activiti.cfg.xml配置文件

    }
    @Test
    public void testCreateProcessEngine2() {
        /**使用配置文件创建工作流需要的23张表*/
        ProcessEngine processEngine = ProcessEngineConfiguration.
                createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    }
    @Test
    public void testCreateProcessEngine3() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    }
    @Autowired
    private  ProcessEngine processEngine;// = ProcessEngines.getDefaultProcessEngine();
    @Autowired
    private  TaskService taskService;
    /**
     * 发布流程
     */
    @Test
    public void deployProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
            repositoryService.createDeployment()
                    .name("myp1")
                    .addClasspathResource("diagrams/kkk.bpmn")
                    .deploy();
    }
    /**
     * 启动流程
     */
    @Test
    public void startProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //可根据id、key、message启动流程
        runtimeService.startProcessInstanceByKey("myProcess_1");
    }
    /**
     * 查看任务
     */
    @Test
    public void queryTask() {
        //TaskService taskService = processEngine.getTaskService();
        //根据assignee（代理人）查询任务
        String assignee = "emp";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        int size = tasks.size();
        for(int i = 0;i < size ; i++){
            Task task = tasks.get(i);
        }
        for(Task task : tasks){
            System.out.println("taskId:"+ task.getId() +
                    ",taskName:"+ task.getName()+
                    ",assignee:"+ task.getAssignee()+
                    ",createTime:"+task.getCreateTime());
        }
    }

    /**
     * 办理任务
     */
    @Test
    public void handleTask() {
        TaskService taskService = processEngine.getTaskService();
        //根据上一步生成的taskId执行任务
        String taskId = "20002";
        taskService.complete(taskId);
    }

    /**
     * 查询流程定义
     */
    @Test
    public void queryProcessDefinition() {
        List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService().createProcessDefinitionQuery().orderByDeploymentId().asc().list();
        for(ProcessDefinition processDefinition : processDefinitions){
            System.out.println(processDefinition.getId()+"---"+processDefinition.getName());
        }
        processDefinitions.stream().forEach(System.out::println);
    }

}
