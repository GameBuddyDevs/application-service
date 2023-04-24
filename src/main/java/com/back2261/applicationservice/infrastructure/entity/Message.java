package com.back2261.applicationservice.infrastructure.entity;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "messages")
public class Message implements Serializable {

    private String id;
    private String sender;
    private String receiver;
    private String messageBody;
    private String date;
    private boolean read;
}
