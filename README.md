# Лабораторная работа 1 - Счетчик слов

В произвольном текстовом документе посчитать и напечатать в консоли сколько раз встречается каждое слово. Текст можно сформировать генератором lorem ipsum. Необходимо использовать регулярные выражения (regexp), должны корректно обрабатываться любые знаки препинания в любом количестве.


# Лабораторная работа 2 - Поисковый робот для исходного текста программы
Для Java проекта (локальной папки) построить и напечатать в консоли обратный индекс наследования классов. Для каждого класса необходимо найти (напечатать) классы, для которых он является базовым (родительским). Должны корректно обрабатываться ключевые слова class, interface, extends, implements. Необходимо использовать интерфейс Map, метод getOrDefault(). Желательно использовать Stream API.

# Лабораторная работа 3 - Поисковый робот с независимыми потоками
Усовершенствовать программу из задания 2, чтобы для обработки каждого файла исходного текста создавался отдельный поток (Thread). Взаимодействия потоков не требуется! Для ожидания завершения потоков можно использовать метод join(), желательно CountDownLatch. Работоспособность программы должна быть продемонстрирована на большом проекте с GitHub, например, Spring Framework.