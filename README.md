# Parallel loader
>Приложение для многопоточной загрузки файла с сервера по URL, **для работы нужен Java FX**
- Программа через GET запрос получает порцию данных и делит между 6-ю потоками
- В файле **settings.properties** можно задать URL и путь для загрузки по умолчанию
- Всегда работает logger и сохраняет данные в logs.log