package vn.ptit.model.config.properties;

import vn.ptit.model.constants.BaseCommonConstants;
import vn.ptit.model.constants.BasePropertiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

/**
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#I18N_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@SuppressWarnings({"ConfigurationProperties", "unused"})
@ConfigurationProperties(prefix = BasePropertiesConstants.I18N_CONFIG)
public class I18nProps {

    private String[] baseNames;
    private String encoding;

    public String[] getBaseNames() {
        return Optional.ofNullable(baseNames).orElse(BaseCommonConstants.DEFAULT_MESSAGE_SOURCE_BASENAME);
    }

    public void setBaseNames(String[] baseNames) {
        this.baseNames = baseNames;
    }

    public String getEncoding() {
        return Optional.ofNullable(encoding).orElse(BaseCommonConstants.DEFAULT_MESSAGE_SOURCE_ENCODING);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
