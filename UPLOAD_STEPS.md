# خطوات رفع المشروع إلى GitHub الجديد

## الخطوة 1: تهيئة Git
افتح موجه الأوامر في مجلد المشروع ونفذ:
```bash
git init
```

## الخطوة 2: إضافة الملفات
```bash
git add .
```

## الخطوة 3: إنشاء أول commit
```bash
git commit -m "Initial commit - WhatsApp Status Saver"
```

## الخطوة 4: ربط بالمستودع الجديد
**أولاً:** اذهب إلى github.com وأنشئ مستودع جديد باسم `save_status`

**ثم:** استبدل `yourusername` باسم المستخدم الخاص بك:
```bash
git branch -M main
git remote add origin https://github.com/yourusername/save_status.git
```

## الخطوة 5: رفع الكود
```bash
git push -u origin main
```

## الخطوة 6: تشغيل البناء
1. اذهب إلى صفحة مستودعك على GitHub
2. اضغط على تبويب "Actions"
3. اختر "Build Android APK"
4. اضغط على "Run workflow"
5. انتظر 5-10 دقائق

## الخطوة 7: تحميل APK
1. عندما ينتهي البناء، اذهب إلى "Artifacts"
2. حمل ملف "debug-apk"
3. انقله إلى هاتفك وقم بتثبيته

## ملاحظات هامة:
- تأكد من أنك مسجل الدخول بحسابك الجديد
- استبدل `yourusername` بالاسم الفعلي لحسابك
- إذا طلب كلمة مرور، أدخل كلمة مرور GitHub ورمز المصادقة الثنائية (إذا مفعّلة)
