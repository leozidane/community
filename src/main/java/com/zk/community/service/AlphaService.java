package com.zk.community.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AlphaService {

    private static Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save() {
        transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                return null;
            }
        });
    }

    @Async
    public void execute2() {
        logger.debug("execute2");
    }

//    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute() {
        logger.debug("execute");
    }
}
