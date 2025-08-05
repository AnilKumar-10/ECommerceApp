package com.ECommerceApp.Repository.User;

import com.ECommerceApp.Model.User.UserActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActivityRepository extends MongoRepository<UserActivityLog,String > {
}
