package com.atik_faysal.model;

public class MemBalanceModel
{
        String name,id,balance;
        public MemBalanceModel(String id,String name,String balance)
        {
                this.name = name;
                this.id = id;
                this.balance = balance;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getBalance() {
                return balance;
        }

        public void setBalance(String balance) {
                this.balance = balance;
        }
}
