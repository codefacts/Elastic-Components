select r.ID, r.NAME, d.NAME, a1.ID, a1.NAME, a2.ID, a2.NAME, a1.ID, a1.NAME, g.ID, a3.NAME, a4.NAME, a5.NAME, a4.NAME, a5.NAME, a4.NAME
from EMPLOYEE r inner join GROUP g on r.ID = g.EMPLOYEE_ID inner join EMPLOY_DESIGNATION a6 on r.ID = a6.EMPLOYEE_ID
inner join DESIGNATION d on a6.DESIGNATION_ID = d.ID inner join DESIGNATION a1 on r.DESIGNATION_ID = a1.ID
inner join DESIGNATION a2 on r.DESIGNATION2_ID = a2.ID
inner join EMPLOYEE a3 on g.EMPLOYEE_ID = a3.ID
inner join DESIGNATION a5 on a3.DESIGNATION2_ID = a5.ID
inner join DESIGNATION a4 on a3.DESIGNATION_ID = a4.ID