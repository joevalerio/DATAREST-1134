package org.springframework.sdr.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(indexName="base")
public abstract class BaseEntity
{
    @Id
    private String id;

    private String createdBy;

    private String type;

    private String name;

    private String desc;

}
