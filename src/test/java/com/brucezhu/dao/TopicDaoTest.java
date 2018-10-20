package com.brucezhu.dao;

import com.brucezhu.domain.Topic;
import com.brucezhu.domain.User;
import com.brucezhu.test.dataset.util.XlsDataSetBeanFactory;
import org.testng.annotations.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

/**
 * topic 的DAO类
 *
 */
public class TopicDaoTest extends BaseDaoTest {

	@SpringBean("topicDao")
	private TopicDao topicDao;
	
	@Test
	@DataSet("XiaoChun.SaveTopics.xls")
	@ExpectedDataSet("XiaoChun.ExpectedTopics.xls")
	public void addTopic()throws Exception {
	    List<Topic> topics = XlsDataSetBeanFactory.createBeans(TopicDaoTest.class,"XiaoChun.SaveTopics.xls", "t_topic", Topic.class);
	    for(Topic topic:topics){
			User user = new User();
			user.setUserId(1);
			topic.setUser(user);
			System.out.println("=============创建时间:" + topic + "\n\n");
	    	topicDao.save(topic);
	    }
	}
}
