import org.hibernate.dialect.DerbyDialect;
import org.hibernate.id.IdentityGenerator;


public class GlassfishDerbyDialect extends DerbyDialect {

    public GlassfishDerbyDialect() {

    }

    @Override
    public Class getNativeIdentifierGeneratorClass() {
        
        return IdentityGenerator.class;
    }

    
    
}
