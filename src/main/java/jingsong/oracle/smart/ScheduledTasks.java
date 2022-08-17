/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jingsong.oracle.smart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

@Component
public class ScheduledTasks {
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	@Resource
	ScaleRule scaleRule;
	@Resource
	ScaleSvc scaleSvc;
	@Resource
	EmailService emailService;

	@Scheduled(fixedRateString = "${checkRate}")
	public void triggerCpuScaleRule() {
//		scaleSvc.dd();
//		scaleRule.getSignal();
//		event.publishSubject.onNext("run");
//		event.publishSubject.debounce(33, TimeUnit.MICROSECONDS).subscribe();

//		RunCmd cmd=new RunCmd(basecmd);
//		System.out.println(basecmd);
//		;
//		log.info("The time is now {}",  cmd.run());
//		emailService.sendMail("subje","dsdasd");
	}
}
