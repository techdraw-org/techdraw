package org.techdraw.webtool.desks.models;

import org.techdraw.sheets.doc.PageStyle;

import java.util.ArrayList;

/**
 * @author Miroslav Kravec
 */
public class DesksModel {

    public ArrayList<DeskModel> desks;
    public ArrayList<DeskMaterialModel> materials;

    public String documentTitle;
    public String pageHeader;
    public String pageFooter;

    public PageStyle pageStyle;
}
