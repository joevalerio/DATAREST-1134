package org.springframework.sdr.model;

import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Document(indexName = "test")
public class Bar extends BaseEntity {

    public Bar(String name, String desc){
        setName(name);
        setDesc(desc);
    }


}
