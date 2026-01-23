package cn.zzb.mybatis.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public enum JdbcType {

    INTEGER(Types.INTEGER),
    FLOAT(Types.FLOAT),
    DOUBLE(Types.DOUBLE),
    DECIMAL(Types.DECIMAL),
    VARCHAR(Types.VARCHAR),
    TIMESTAMP(Types.TIMESTAMP);

    private final int TYPE_CODE;
    private static final Map<Integer, JdbcType> codeLookup = new HashMap<>();

    JdbcType(int typeCode) {
       this.TYPE_CODE = typeCode;
    }

    static{
        for(JdbcType type : JdbcType.values()) {
            codeLookup.put(type.TYPE_CODE, type);
        }
    }

    public static JdbcType forCode(int code){
        return codeLookup.get(code);
    }
}
