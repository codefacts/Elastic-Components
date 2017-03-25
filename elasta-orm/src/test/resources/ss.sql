select r.EID, r.ENAME, r.DEG, r.SALARY, a1.ID, a1.NAME, a2.ID, a2.NAME, a3.ID, a3.NAME, d.ID, d.NAME, a4.ID, a4.NAME, a5.ID, a5.NAME
from (((((((employee r left join EMPLOYEE_DEPARTMENT a6 on r.EID = a6.EMPLOYEE_EID) inner join DEPARTMENT d on a6.DEPARTMENTS_ID = d.ID)
inner join DEPARTMENT a1 on r.DEPARTMENT_ID = a1.ID)
inner join DEPARTMENT a2 on a1.DEPARTMENT_ID = a2.ID)
inner join DEPARTMENT a3 on a2.DEPARTMENT_ID = a3.ID)
inner join DEPARTMENT a4 on d.DEPARTMENT_ID = a4.ID)
inner join DEPARTMENT a5 on a4.DEPARTMENT_ID = a5.ID)