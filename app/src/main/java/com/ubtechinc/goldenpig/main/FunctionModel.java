package com.ubtechinc.goldenpig.main;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: ${DESCRIPTION}
 * @Author: zhijunzhou
 * @CreateDate: 2018/12/24 19:52
 */
public class FunctionModel {

    public FunctionCatetory catetory;

    public FunctionStatement statement;

    public class FunctionCatetory {

        public List<CategorysModel> categorys;

        public String md5;

        @Override
        public String toString() {
            return "FunctionCatetory{" +
                    "categorys=" + categorys +
                    ", md5='" + md5 + '\'' +
                    '}';
        }
    }

    public class CategorysModel {

        public String name;

        public String icoUrl;

        public String type;

        public String url;

        @Override
        public String toString() {
            return "CategorysModel{" +
                    "name='" + name + '\'' +
                    ", icoUrl='" + icoUrl + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public class FunctionStatement {

        public String title;

        public String md5;

        public String[] statements;

        @Override
        public String toString() {
            return "FunctionStatement{" +
                    "title='" + title + '\'' +
                    ", md5='" + md5 + '\'' +
                    ", statements=" + Arrays.toString(statements) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FunctionModel{" +
                "catetory=" + catetory +
                ", statement=" + statement +
                '}';
    }
}
