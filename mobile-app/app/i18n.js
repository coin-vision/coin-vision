import RNLanguages from 'react-native-languages';
import i18n from 'i18n-js';

import de from './translations/de.json';
import en from './translations/en.json';
import es from './translations/es.json';
import fr from './translations/fr.json';
import it from './translations/it.json';
import ru from './translations/ru.json';
import uk from './translations/uk.json';
import zh from './translations/zh.json';
import pt from './translations/pt.json';

i18n.locale = RNLanguages.language;
i18n.fallbacks = true;
i18n.translations = {de, en, es, fr, it, ru, uk, zh, pt};

export default i18n;
