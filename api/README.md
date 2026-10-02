# API مرکز راهکار هوشمند ایرانیان

REST API فعلی بدون دیتابیس و با ذخیره‌سازی JSON است؛ در مرحله بعد storage می‌تواند بدون تغییر قرارداد API با PostgreSQL یا MySQL جایگزین شود.

## اجرا
npm start

پیش‌فرض: http://localhost:8080

## Endpointها
GET /api/v1/health
GET /api/v1/dashboard
GET /api/v1/records
POST /api/v1/records
GET /api/v1/records/:id
PATCH /api/v1/records/:id
GET /api/v1/suppliers
GET /api/v1/projects
GET /api/v1/reports

برای تست روی گوشی، API باید روی یک آدرس HTTPS قابل دسترس از گوشی اجرا شود و همان Base URL در اپ قرار بگیرد.
