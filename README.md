# Application for tutors in JavaFX
>This app was created for tutors to record savings, earnings, how many students they have
>and store information about their lessons.

## How to use it?
It's basically an app to make your life as a tutor easier. You can switch between pages: 
* Main Page - which shows you statistics and lists off all registered "objects", it look like that 
(it currently shows table of payments, you can switch them with buttons below table)
![](/screenshots/mainPagePayments.png)

* Lessons - here you can add, edit or remove yours lessons and also open table with lessons
![](/screenshots/addingLesson.png)
![](/screenshots/lessonsInTable.PNG)

* Students - here you can add, edit or remove yours students and also open table with students
![](/screenshots/addingStudent.png)

* Payments - here you can add, edit or remove yours payments and also open table with payments
![](/screenshots/editingPayment.png)

* Savings - this page allows you to deposit savings (if you really are saving money, you are not allowed to edit or remove money that you saved), it also shows statistics 
(one of them is telling you how much money you saved out of 10 000 zl), you can open table with savings here as well
![](/screenshots/savingsPage.PNG)

* Calendar - here is calendar planned for years 2023-2027, it has colored days 
(purple ones are official holidays in Poland, and orange ones are your added lessons)
![](/screenshots/calendarHolidayTooltip.png)
![](/screenshots/calendarLessonTooltip.png)

There are search bars and filters (by date) in various places.

## What do I need to start?
* IntelliJ
* Docker
* this repository :)

## How to start the app?
1. You need to shut down databases or other resources that use 3306 port.
2. Open this repository in IntelliJ.
3. Open Docker and terminal in directory where you store my repository, then type this in your terminal.
```powershell
docker compose up --build
```
4. The app should be ready to run so enjoy!
