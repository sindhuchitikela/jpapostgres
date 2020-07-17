package com.pg.demo.category.dataobject;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pg.demo.category.enu.CategoryLevel;
import com.pg.demo.category.enu.CategoryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ofr_category")
@Getter
@Setter
@NoArgsConstructor
public class Category {
  @Id
  @Column(name = "c_id", updatable = true, unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ")
  @SequenceGenerator(sequenceName = "CATEGORY_ID_SEQ", allocationSize = 1, name = "CATEGORY_SEQ")
  private Long id;

  @Column(name = "c_name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_level")
  private CategoryLevel level;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private CategoryStatus status;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "updated_user")
  private String updatedUser;

  @Column(name = "updated_time")
  private LocalDateTime updatedTime;

  @JsonBackReference
  //DEV NOTES: default fetch type is eager
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", insertable = false, updatable = false)
  private Category parentCategory;

  @JsonManagedReference
  //DEV NOTES: default fetch type is lazy
  @OneToMany(mappedBy = "parentCategory")
  private List<Category> childCategories;

  @Override
  public String toString() {
    return "id: " + this.id + " name: " + this.name;
  }

  public Category getParentCategory() {
    return parentCategory;
  }

}
