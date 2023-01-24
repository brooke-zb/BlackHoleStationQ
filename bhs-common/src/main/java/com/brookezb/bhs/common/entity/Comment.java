package com.brookezb.bhs.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

/**
 * @author brooke_zb
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("0")
public class Comment extends AbstractComment {

    /**
     * 子评论
     */
    @OneToMany(mappedBy = "parent")
    @Fetch(FetchMode.SUBSELECT)
    private List<SubComment> children;
}
