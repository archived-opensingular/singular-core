package br.net.mirante.singular;

import br.net.mirante.singular.persistence.entity.Actor;

public class ConstantesUtil {

    public static final Actor USER_1 = new Actor() {
        @Override
        public Long getCod() {
            return 1l;
        }

        @Override
        public String getNomeGuerra() {
            return "User_1";
        }

        @Override
        public String getEmail() {
            return "user1@gmail.com";
        }
    };

    public static final Actor USER_2 = new Actor() {
        @Override
        public Long getCod() {
            return 2l;
        }

        @Override
        public String getNomeGuerra() {
            return "User_2";
        }

        @Override
        public String getEmail() {
            return "user2@gmail.com";
        }
    };

    public static final Actor USER_3 = new Actor() {
        @Override
        public Long getCod() {
            return 3l;
        }

        @Override
        public String getNomeGuerra() {
            return "User_3";
        }

        @Override
        public String getEmail() {
            return "user3@gmail.com";
        }
    };

    public static final Actor USER_4 = new Actor() {
        @Override
        public Long getCod() {
            return 4l;
        }

        @Override
        public String getNomeGuerra() {
            return "User_4";
        }

        @Override
        public String getEmail() {
            return "user4@gmail.com";
        }
    };
    
}
