package com.hatsukoi.eshopblvd.product.entity;

import java.util.ArrayList;
import java.util.List;

public class AttrExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public AttrExample() {
        oredCriteria = new ArrayList<>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andAttrIdIsNull() {
            addCriterion("attr_id is null");
            return (Criteria) this;
        }

        public Criteria andAttrIdIsNotNull() {
            addCriterion("attr_id is not null");
            return (Criteria) this;
        }

        public Criteria andAttrIdEqualTo(Long value) {
            addCriterion("attr_id =", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdNotEqualTo(Long value) {
            addCriterion("attr_id <>", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdGreaterThan(Long value) {
            addCriterion("attr_id >", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdGreaterThanOrEqualTo(Long value) {
            addCriterion("attr_id >=", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdLessThan(Long value) {
            addCriterion("attr_id <", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdLessThanOrEqualTo(Long value) {
            addCriterion("attr_id <=", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdIn(List<Long> values) {
            addCriterion("attr_id in", values, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdNotIn(List<Long> values) {
            addCriterion("attr_id not in", values, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdBetween(Long value1, Long value2) {
            addCriterion("attr_id between", value1, value2, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdNotBetween(Long value1, Long value2) {
            addCriterion("attr_id not between", value1, value2, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrNameIsNull() {
            addCriterion("attr_name is null");
            return (Criteria) this;
        }

        public Criteria andAttrNameIsNotNull() {
            addCriterion("attr_name is not null");
            return (Criteria) this;
        }

        public Criteria andAttrNameEqualTo(String value) {
            addCriterion("attr_name =", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameNotEqualTo(String value) {
            addCriterion("attr_name <>", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameGreaterThan(String value) {
            addCriterion("attr_name >", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameGreaterThanOrEqualTo(String value) {
            addCriterion("attr_name >=", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameLessThan(String value) {
            addCriterion("attr_name <", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameLessThanOrEqualTo(String value) {
            addCriterion("attr_name <=", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameLike(String value) {
            addCriterion("attr_name like", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameNotLike(String value) {
            addCriterion("attr_name not like", value, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameIn(List<String> values) {
            addCriterion("attr_name in", values, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameNotIn(List<String> values) {
            addCriterion("attr_name not in", values, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameBetween(String value1, String value2) {
            addCriterion("attr_name between", value1, value2, "attrName");
            return (Criteria) this;
        }

        public Criteria andAttrNameNotBetween(String value1, String value2) {
            addCriterion("attr_name not between", value1, value2, "attrName");
            return (Criteria) this;
        }

        public Criteria andSearchTypeIsNull() {
            addCriterion("search_type is null");
            return (Criteria) this;
        }

        public Criteria andSearchTypeIsNotNull() {
            addCriterion("search_type is not null");
            return (Criteria) this;
        }

        public Criteria andSearchTypeEqualTo(Byte value) {
            addCriterion("search_type =", value, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeNotEqualTo(Byte value) {
            addCriterion("search_type <>", value, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeGreaterThan(Byte value) {
            addCriterion("search_type >", value, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("search_type >=", value, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeLessThan(Byte value) {
            addCriterion("search_type <", value, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeLessThanOrEqualTo(Byte value) {
            addCriterion("search_type <=", value, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeIn(List<Byte> values) {
            addCriterion("search_type in", values, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeNotIn(List<Byte> values) {
            addCriterion("search_type not in", values, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeBetween(Byte value1, Byte value2) {
            addCriterion("search_type between", value1, value2, "searchType");
            return (Criteria) this;
        }

        public Criteria andSearchTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("search_type not between", value1, value2, "searchType");
            return (Criteria) this;
        }

        public Criteria andIconIsNull() {
            addCriterion("icon is null");
            return (Criteria) this;
        }

        public Criteria andIconIsNotNull() {
            addCriterion("icon is not null");
            return (Criteria) this;
        }

        public Criteria andIconEqualTo(String value) {
            addCriterion("icon =", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotEqualTo(String value) {
            addCriterion("icon <>", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconGreaterThan(String value) {
            addCriterion("icon >", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconGreaterThanOrEqualTo(String value) {
            addCriterion("icon >=", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLessThan(String value) {
            addCriterion("icon <", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLessThanOrEqualTo(String value) {
            addCriterion("icon <=", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLike(String value) {
            addCriterion("icon like", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotLike(String value) {
            addCriterion("icon not like", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconIn(List<String> values) {
            addCriterion("icon in", values, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotIn(List<String> values) {
            addCriterion("icon not in", values, "icon");
            return (Criteria) this;
        }

        public Criteria andIconBetween(String value1, String value2) {
            addCriterion("icon between", value1, value2, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotBetween(String value1, String value2) {
            addCriterion("icon not between", value1, value2, "icon");
            return (Criteria) this;
        }

        public Criteria andValueSelectIsNull() {
            addCriterion("value_select is null");
            return (Criteria) this;
        }

        public Criteria andValueSelectIsNotNull() {
            addCriterion("value_select is not null");
            return (Criteria) this;
        }

        public Criteria andValueSelectEqualTo(String value) {
            addCriterion("value_select =", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectNotEqualTo(String value) {
            addCriterion("value_select <>", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectGreaterThan(String value) {
            addCriterion("value_select >", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectGreaterThanOrEqualTo(String value) {
            addCriterion("value_select >=", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectLessThan(String value) {
            addCriterion("value_select <", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectLessThanOrEqualTo(String value) {
            addCriterion("value_select <=", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectLike(String value) {
            addCriterion("value_select like", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectNotLike(String value) {
            addCriterion("value_select not like", value, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectIn(List<String> values) {
            addCriterion("value_select in", values, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectNotIn(List<String> values) {
            addCriterion("value_select not in", values, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectBetween(String value1, String value2) {
            addCriterion("value_select between", value1, value2, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andValueSelectNotBetween(String value1, String value2) {
            addCriterion("value_select not between", value1, value2, "valueSelect");
            return (Criteria) this;
        }

        public Criteria andAttrTypeIsNull() {
            addCriterion("attr_type is null");
            return (Criteria) this;
        }

        public Criteria andAttrTypeIsNotNull() {
            addCriterion("attr_type is not null");
            return (Criteria) this;
        }

        public Criteria andAttrTypeEqualTo(Byte value) {
            addCriterion("attr_type =", value, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeNotEqualTo(Byte value) {
            addCriterion("attr_type <>", value, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeGreaterThan(Byte value) {
            addCriterion("attr_type >", value, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("attr_type >=", value, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeLessThan(Byte value) {
            addCriterion("attr_type <", value, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeLessThanOrEqualTo(Byte value) {
            addCriterion("attr_type <=", value, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeIn(List<Byte> values) {
            addCriterion("attr_type in", values, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeNotIn(List<Byte> values) {
            addCriterion("attr_type not in", values, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeBetween(Byte value1, Byte value2) {
            addCriterion("attr_type between", value1, value2, "attrType");
            return (Criteria) this;
        }

        public Criteria andAttrTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("attr_type not between", value1, value2, "attrType");
            return (Criteria) this;
        }

        public Criteria andEnableIsNull() {
            addCriterion("enable is null");
            return (Criteria) this;
        }

        public Criteria andEnableIsNotNull() {
            addCriterion("enable is not null");
            return (Criteria) this;
        }

        public Criteria andEnableEqualTo(Long value) {
            addCriterion("enable =", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotEqualTo(Long value) {
            addCriterion("enable <>", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThan(Long value) {
            addCriterion("enable >", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThanOrEqualTo(Long value) {
            addCriterion("enable >=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThan(Long value) {
            addCriterion("enable <", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThanOrEqualTo(Long value) {
            addCriterion("enable <=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableIn(List<Long> values) {
            addCriterion("enable in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotIn(List<Long> values) {
            addCriterion("enable not in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableBetween(Long value1, Long value2) {
            addCriterion("enable between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotBetween(Long value1, Long value2) {
            addCriterion("enable not between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andCatelogIdIsNull() {
            addCriterion("catelog_id is null");
            return (Criteria) this;
        }

        public Criteria andCatelogIdIsNotNull() {
            addCriterion("catelog_id is not null");
            return (Criteria) this;
        }

        public Criteria andCatelogIdEqualTo(Long value) {
            addCriterion("catelog_id =", value, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdNotEqualTo(Long value) {
            addCriterion("catelog_id <>", value, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdGreaterThan(Long value) {
            addCriterion("catelog_id >", value, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdGreaterThanOrEqualTo(Long value) {
            addCriterion("catelog_id >=", value, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdLessThan(Long value) {
            addCriterion("catelog_id <", value, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdLessThanOrEqualTo(Long value) {
            addCriterion("catelog_id <=", value, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdIn(List<Long> values) {
            addCriterion("catelog_id in", values, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdNotIn(List<Long> values) {
            addCriterion("catelog_id not in", values, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdBetween(Long value1, Long value2) {
            addCriterion("catelog_id between", value1, value2, "catelogId");
            return (Criteria) this;
        }

        public Criteria andCatelogIdNotBetween(Long value1, Long value2) {
            addCriterion("catelog_id not between", value1, value2, "catelogId");
            return (Criteria) this;
        }

        public Criteria andShowDescIsNull() {
            addCriterion("show_desc is null");
            return (Criteria) this;
        }

        public Criteria andShowDescIsNotNull() {
            addCriterion("show_desc is not null");
            return (Criteria) this;
        }

        public Criteria andShowDescEqualTo(Byte value) {
            addCriterion("show_desc =", value, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescNotEqualTo(Byte value) {
            addCriterion("show_desc <>", value, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescGreaterThan(Byte value) {
            addCriterion("show_desc >", value, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescGreaterThanOrEqualTo(Byte value) {
            addCriterion("show_desc >=", value, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescLessThan(Byte value) {
            addCriterion("show_desc <", value, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescLessThanOrEqualTo(Byte value) {
            addCriterion("show_desc <=", value, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescIn(List<Byte> values) {
            addCriterion("show_desc in", values, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescNotIn(List<Byte> values) {
            addCriterion("show_desc not in", values, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescBetween(Byte value1, Byte value2) {
            addCriterion("show_desc between", value1, value2, "showDesc");
            return (Criteria) this;
        }

        public Criteria andShowDescNotBetween(Byte value1, Byte value2) {
            addCriterion("show_desc not between", value1, value2, "showDesc");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table pms_attr
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table pms_attr
     *
     * @mbg.generated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}