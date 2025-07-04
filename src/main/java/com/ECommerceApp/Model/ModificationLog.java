package com.ECommerceApp.Model;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ModificationLog {
    private Date timestamp;
    private String updatedBy;
    private Map<String, Map<String, Object>> changes;
}
