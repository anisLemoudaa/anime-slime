# Server resolvers

تستخدم شاشة تفاصيل الحلقة `ServerResolverRegistry` لاختيار المحول بحسب المضيف، ثم تعرض الروابط المرتجعة قبل فتح الحلقة.

| Resolver | المصدر | النتيجة |
|---|---|---|
| `DonghuaServerResolver` | Donghua-Ar | يطلب `episodes.getByNumber` ويعطي MP4 ثم MKV ثم Filemoon عند توفرها |
| `WitAnimeServerResolver` | WitAnime | يحلل صفحة الحلقة و`video/source` و`iframe` وواجهة `sourcesUrl`، ثم يستخدم صفحة المشاهدة كـfallback |
| `AnimeLekServerResolver` | Animelek / Animedar | يستخرج `m3u8` و`mp4` وiframe وروابط data attributes، ثم يستخدم صفحة الحلقة كـfallback |

لا يتم اعتبار روابط `available` الوهمية روابط تشغيل. في Donghua-Ar يتم قبول الرابط فقط عندما يكون URL حقيقيًا، وبذلك لا يتم إرسال قيمة حالة بدل ملف فيديو إلى Media3.
