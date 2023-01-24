package com.brookezb.bhs.common.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import com.github.yitter.contract.IIdGenerator;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.DefaultIdGenerator;

import java.io.Serializable;

/**
 * @author brooke_zb
 */
public class SnowflakeIdGenerator implements IdentifierGenerator {
    private static final IIdGenerator idGenInstance;

    static {
        IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
        idGenInstance = new DefaultIdGenerator(options);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return idGenInstance.newLong();
    }
}
