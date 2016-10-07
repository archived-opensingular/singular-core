/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.dao.form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

import org.apache.wicket.model.IModel;

@Entity
@Table(name = "EXAMPLE_DATA")
@SuppressWarnings({"rawtypes", "serial"})
public class ExampleDataDTO implements IModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type;

    @Column
    private String description;

    @Lob
    @Column
    private String xml;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date editionDate;

    @Lob
    @Column
    private String annotations;

    public ExampleDataDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getAnnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnnotations() {
        return annotations;
    }

    public Date getEditionDate() {
        return editionDate;
    }

    public void setEditionDate(Date editionDate) {
        this.editionDate = editionDate;
    }

    @Override
    public void detach() {

    }

    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public void setObject(Object arg0) {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
