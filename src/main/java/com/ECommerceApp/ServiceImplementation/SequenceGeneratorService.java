package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.Model.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;


@Service
public class SequenceGeneratorService {
    @Autowired
    private  MongoTemplate mongoTemplate;
// this is used to get the next id value
    public long getNextSequence(String counterName) {
        Query query = new Query(Criteria.where("_id").is(counterName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Counter counter = mongoTemplate.findAndModify(query, update, options, Counter.class);
        return counter != null ? counter.getSeq() : 101001; // Fallback default
    }
}
