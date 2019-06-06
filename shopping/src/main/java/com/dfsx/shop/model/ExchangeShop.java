package com.dfsx.shop.model;

import java.io.Serializable;

/**
 * Created by heyang on 207/11/16
 *
 *
 *  兑换商品
 */
final public class ExchangeShop implements Serializable {


    /**
     * count : 0
     * contact : {"id":-1,"name":"","phone_number":"","address":""}
     */

    private int count;
    private ContactBean contact;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ContactBean getContact() {
        return contact;
    }

    public void setContact(ContactBean contact) {
        this.contact = contact;
    }

    public static class ContactBean {
        /**
         * id : -1
         * name :
         * phone_number :
         * address :
         */

        private long id;
        private String name;
        private String phone_number;
        private String address;
        private boolean  last_used;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isLast_used() {
            return last_used;
        }

        public void setLast_used(boolean last_used) {
            this.last_used = last_used;
        }

    }
}
