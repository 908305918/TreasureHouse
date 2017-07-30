package com.zizoy.treasurehouse.bean;

import java.util.List;

/**
 * Created by 科技 on 2017/2/14.
 */
public class HousePropertyBean {
    /**
     * phone : 18546585858
     * updatetime :
     * pid : 139
     * type : 房产
     * contact : 地方
     * url : http://118.89.222.210:8080/cbg/upload/2017032820101490703015354.jpg
     * content : 单间 | 回龙观龙腾苑四区，正规次卧，有空调，地铁13号线边上
     * title : 单间 | 回龙观龙腾苑四区，正规次卧，有空调，地铁13号线边上
     * price : 13
     * readernum : 15
     * pcreatetime : 2017-03-28 20:10:15
     * postattachList : [{"postid":139,"paid":58,"attachid":60,"attachment":{"oname":"1b8b2924faaf8043","name":"2017032820101490703015354","path":"/usr/share/tomcat7/webapps/cbg/upload/","aid":60,"fullpath":"","suffix":"jpg","acreatetime":"2017-03-28 20:10:15","size":79809}}]
     * address : 单间 | 回龙观龙腾苑四区，正规次卧，有空调，地铁13号线边上
     * type2 : 出租
     * longitude : 116.336726
     * latitude : 40.000077
     */

    private String phone;
    private String updatetime;
    private String pid;
    private String type;
    private String contact;
    private String url;
    private String content;
    private String title;
    private String price;
    private String readernum;
    private String pcreatetime;
    private String address;
    private String type2;
    private String longitude;
    private String latitude;
    private List<PostattachListBean> postattachList;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getReadernum() {
        return readernum;
    }

    public void setReadernum(String readernum) {
        this.readernum = readernum;
    }

    public String getPcreatetime() {
        return pcreatetime;
    }

    public void setPcreatetime(String pcreatetime) {
        this.pcreatetime = pcreatetime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public List<PostattachListBean> getPostattachList() {
        return postattachList;
    }

    public void setPostattachList(List<PostattachListBean> postattachList) {
        this.postattachList = postattachList;
    }

    public static class PostattachListBean {
        /**
         * postid : 139
         * paid : 58
         * attachid : 60
         * attachment : {"oname":"1b8b2924faaf8043","name":"2017032820101490703015354","path":"/usr/share/tomcat7/webapps/cbg/upload/","aid":60,"fullpath":"","suffix":"jpg","acreatetime":"2017-03-28 20:10:15","size":79809}
         */

        private String postid;
        private String paid;
        private String attachid;
        private AttachmentBean attachment;

        public String getPostid() {
            return postid;
        }

        public void setPostid(String postid) {
            this.postid = postid;
        }

        public String getPaid() {
            return paid;
        }

        public void setPaid(String paid) {
            this.paid = paid;
        }

        public String getAttachid() {
            return attachid;
        }

        public void setAttachid(String attachid) {
            this.attachid = attachid;
        }

        public AttachmentBean getAttachment() {
            return attachment;
        }

        public void setAttachment(AttachmentBean attachment) {
            this.attachment = attachment;
        }

        public static class AttachmentBean {
            /**
             * oname : 1b8b2924faaf8043
             * name : 2017032820101490703015354
             * path : /usr/share/tomcat7/webapps/cbg/upload/
             * aid : 60
             * fullpath :
             * suffix : jpg
             * acreatetime : 2017-03-28 20:10:15
             * size : 79809
             */

            private String oname;
            private String name;
            private String path;
            private int aid;
            private String fullpath;
            private String suffix;
            private String acreatetime;
            private int size;

            public String getOname() {
                return oname;
            }

            public void setOname(String oname) {
                this.oname = oname;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public int getAid() {
                return aid;
            }

            public void setAid(int aid) {
                this.aid = aid;
            }

            public String getFullpath() {
                return fullpath;
            }

            public void setFullpath(String fullpath) {
                this.fullpath = fullpath;
            }

            public String getSuffix() {
                return suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public String getAcreatetime() {
                return acreatetime;
            }

            public void setAcreatetime(String acreatetime) {
                this.acreatetime = acreatetime;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }
        }
    }
}
