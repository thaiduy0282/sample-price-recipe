package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag implements Comparable<Tag> {

    private String id;
    public Map<String, Object> customFields;
    private String status = StringUtils.EMPTY;
    public boolean isActive = true;
    private String name = StringUtils.EMPTY;

    private String description = StringUtils.EMPTY;
    private String createdBy = StringUtils.EMPTY;
    private Long createdDate = 0L;
    private String lastModifiedBy = StringUtils.EMPTY;
    private Long lastModifiedDate = 0L;
    private String externalId;
    private String bg;

    @Override
    public int compareTo(Tag o) {
        return this.getLastModifiedDate().compareTo(o.getLastModifiedDate());
    }




	public Tag(String name) {
		super();
		this.name = name;
	}

//    private List<TagObjectReference> tagObjectReferences;
}
