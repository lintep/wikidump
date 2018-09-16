package com.lintep.wikidump;


import tools.util.Str;
import tools.util.collection.iterator.JsonUpdatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WikiPage implements JsonUpdatable {

    String title;
    String ns;
    String id;
    String parentId;
    String timestamp;
    String comment;
    String text;

    String revisionId;
    String revisionParentId;
    String revisionTimestamp;
    String revisionComment;
    String revisionText;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        if (text == null)
            return "";
        else
            return text;
    }

    public boolean isFilterForNlp(){
        if(isRedirect()){
            return true;
        }

        if(this.title.indexOf(":")>=0){
            return true;
        }

        return false;
    }

    public int getTextLength() {
        if (text == null && revisionText == null)
            return 0;
        else if (text == null)
            return revisionText.length();
        else if (revisionText == null)
            return text.length();
        else
            return text.length() + revisionText.length();
    }

    public void setRevisionText(String revisionText) {
        this.revisionText = revisionText;
    }

    public String getRevisionText() {
        return revisionText;
    }

    public WikiPage(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public String getRevisionParentId() {
        return revisionParentId;
    }

    public void setRevisionParentId(String revisionParentId) {
        this.revisionParentId = revisionParentId;
    }

    public String getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(String revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    public String getRevisionComment() {
        return revisionComment;
    }

    public void setRevisionComment(String revisionComment) {
        this.revisionComment = revisionComment;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void load(Map<String, String> jsonMap) {
        this.id = (String) jsonMap.get("id");
        this.title = (String) jsonMap.get("title");
        if (jsonMap.containsKey("ns"))
            this.ns = (String) jsonMap.get("ns");
        else
            this.ns = null;

        if (jsonMap.containsKey("parentid"))
            this.parentId = (String) jsonMap.get("parentid");
        else
            this.parentId = null;

        if (jsonMap.containsKey("comment"))
            this.comment = (String) jsonMap.get("comment");
        else
            this.comment = null;

        if (jsonMap.containsKey("timestamp"))
            this.timestamp = (String) jsonMap.get("timestamp");
        else
            this.timestamp = null;

        if (jsonMap.containsKey("text"))
            this.text = (String) jsonMap.get("text");
        else
            this.text = null;

        if (jsonMap.containsKey("revisioncomment"))
            this.revisionComment = (String) jsonMap.get("revisioncomment");
        else
            this.revisionComment = null;

        if (jsonMap.containsKey("revisionid"))
            this.revisionId = (String) jsonMap.get("revisionid");
        else
            this.revisionId = null;

        if (jsonMap.containsKey("revisionparentid"))
            this.revisionParentId = (String) jsonMap.get("revisionparentid");
        else
            this.revisionParentId = null;

        if (jsonMap.containsKey("revisiontext"))
            this.revisionText = (String) jsonMap.get("revisiontext");
        else
            this.revisionText = null;

    }

    @Override
    public String toString() {
        return "id(" + this.id + ") " + this.title;
    }

    public boolean containText() {
        return Str.isEmpty(this.text);
    }

    public boolean containComment() {
        return Str.isEmpty(this.comment);
    }

    public boolean containRevisionComment() {
        return Str.isEmpty(this.revisionComment);
    }

    public boolean containRevisionText() {
        return Str.isEmpty(this.revisionText);
    }

    public Map<String, String> getDataMap() {
        HashMap<String, String> fieldValues = new HashMap<String, String>();
        getDataMap(fieldValues);
        return fieldValues;
    }

    public void getDataMap(Map<String, String> fieldValues) {
        fieldValues.put("id", this.id);
        fieldValues.put("title", this.title);
        if (this.text != null)
            fieldValues.put("text", this.text);
        if (this.ns != null)
            fieldValues.put("ns", this.ns);
        if (this.parentId != null)
            fieldValues.put("parentid", this.parentId);
        if (this.comment != null)
            fieldValues.put("comment", this.comment);
        if (this.timestamp != null)
            fieldValues.put("timestamp", this.timestamp);

        if (this.revisionText != null)
            fieldValues.put("revisiontext", this.revisionText);
        if (this.revisionComment != null)
            fieldValues.put("revisioncomment", this.revisionComment);
        if (this.revisionId != null)
            fieldValues.put("revisionid", this.revisionId);
        if (this.revisionParentId != null)
            fieldValues.put("revisionparentid", this.revisionParentId);
    }

    @Override
    public void update(String jsonString) {
        // TODO Auto-generated method stub
    }



    public boolean isRedirect() {
        if (this.text!=null) {
            if (this.text.toLowerCase().indexOf("#redirect")==0
                    || this.text.indexOf("#تغییرمسیر")==0 || this.text.indexOf("#تغییر_مسیر")==0){
                return true;
            }
        }
        return false;
    }

    public List<String> getTitleLinks() {
        List<String> result=new ArrayList<>();
        String rawText = getText();
        try {
            ArrayList<String> tags = Str.returnBetweenStartEndLabelRecursive(WikiPagePartitioned.clearText(rawText), "[[", "]]");
            tags.forEach(s -> result.add(s));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
