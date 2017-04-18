/**
 * 
 */
package com.cip.ferrari.admin.service.alarm;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cip.ferrari.admin.alarm.mail.MailEntity;
import com.cip.ferrari.admin.service.notify.MailService;

/**
 * @author yuantengkai
 *
 */
@Service
public class DefaultAlarmServiceImpl implements AlarmService {

    @Resource
    private MailService mailSender;

    @Override
    public void sendMail(String[] toAddr, String subject, String content) {
        if (toAddr == null) {
            return;
        }
        MailEntity mail = new MailEntity();
        mail.setToAddress(toAddr);
        mail.setSubject(subject);
        mail.setContent(content);
        mail.setContentType("text/plain");
        mailSender.send(mail);
    }

}
