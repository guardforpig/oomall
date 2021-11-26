package cn.edu.xmu.oomall.activity.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class AdvanceSale implements Serializable {

    private Long id;
    private Long shopId;
    private String shopName;
    private String name;
    private LocalDateTime payTime;
    private Long advancePayPrice;
    private Long creatorId;
    private String createName;
    private Long modifierId;
    private String modiName;
    private Byte state;

    public enum state {
        /**
         * OFFLINE为草稿态
         * ONLINE为上线态
         * DELETE为下线态
         */
        OFFLINE((byte)0,"草稿"),
        ONLINE((byte)1,"上线"),
        DELETE((byte)2,"下线");
        private static final Map<Byte, state> TYPE_MAP;
        static {
            TYPE_MAP = new HashMap();
            for (state ss : values()) {
                TYPE_MAP.put(ss.code, ss);
            }
        }
        private byte code;
        private String description;
        state(byte code, String description) {
            this.code = code;
            this.description = description;
        }
        public static state getTypeByCode(Integer code) {
            return TYPE_MAP.get(code);
        }
        public Byte getCode() {
            return code;
        }
        public String getDescription() {
            return description;
        }
    }
}
