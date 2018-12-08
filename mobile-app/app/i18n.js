import RNLanguages from 'react-native-languages';
import i18n from 'i18n-js';

import de from './locales/de.json';
import en from './locales/en.json';
import es from './locales/es.json';
import fr from './locales/fr.json';
import it from './locales/it.json';
import ru from './locales/ru.json';
import uk from './locales/uk.json';
import zh from './locales/zh.json';
import pt from './locales/pt.json';

i18n.locale = RNLanguages.language;
i18n.fallbacks = true;
i18n.translations = {de, en, es, fr, it, ru, uk, zh, pt};

export default i18n;
