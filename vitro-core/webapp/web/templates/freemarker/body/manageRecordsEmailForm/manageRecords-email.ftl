<#--
Copyright (c) 2014, QUT University
All rights reserved.

-->

<#-- Contact form email response -->

<#-- Only manage records dashboard to work in email.  -->

<html>
<body style="background: white">
<table style="margin:0pt auto;width:600px;font:14px Arial, Helvetica, sans-serif;"
        cellspacing="0" cellpadding="10" align="center">
	<tr>
		<td align="center" style="color: white; background-color: #0E487F; font-size:18px; border-top-right-radius: 5px; border-top-left-radius: 5px">Research Data Finder</td>
	</tr>
	<#if errorMsg??>
		<tr height="35px">
			<td align="left" style="color: red; font-size:14px; border-left: 1px solid #BFBFBF;border-right: 1px solid #BFBFBF; line-height:150%">${errorMsg}</td>
		</tr>
	</#if>
	<tr height="35px">
		<td align="left" style="color: black; font-size:14px; border-left: 1px solid #BFBFBF;border-right: 1px solid #BFBFBF; line-height:150%;background-color:#FFFFF">

Dear ${recipientName},<br/><br/>
${paragraph_1}<br/><br/>
<table style="font:14px Arial, Helvetica, sans-serif;" cellspacing="5" cellpadding="0">
	<tr>
		<td align="left" style="font-weight:bold">Record URL:</td>
		<td align="left"><a href="${recordURL}">${recordURL}</a></td>
	</tr>
	<tr>
		<td align="left" style="font-weight:bold">Record Created By:</td>
		<td align="left">${recordCreatedByName}</td>
	</tr>
	<tr>
		<td align="left" style="font-weight:bold">Email:</td>
		<td align="left"><a href="${recordCreatedByEmail}">${recordCreatedByEmail}</a></td>
	</tr>
	<tr>
		<td align="left" style="font-weight:bold">Record Created Date:</td>
		<td align="left">${recordCreatedDate}</td>
	</tr>
	<#if assignedByName??>
		<tr>
			<td align="left" style="font-weight:bold">Assigned by:</td>
			<td align="left">${assignedByName}</td>
		</tr>
	</#if>
	<#if publishedByName??>
		<tr>
			<td align="left" style="font-weight:bold">Published by:</td>
			<td align="left">${publishedByName}</td>
		</tr>
	</#if>
	<#if rejectedByName??>
		<tr>
			<td align="left" style="font-weight:bold">Record Rejected by:</td>
			<td align="left">${rejectedByName}</td>
		</tr>
	</#if>
	<#if rejectedByEmail??>
		<tr>
			<td align="left" style="font-weight:bold">Rejected by Email:</td>
			<td align="left">${rejectedByEmail}</td>
		</tr>
	</#if>
	<#if comments??>
		<tr>
			<td align="left" style="font-weight:bold">Comments:</td>
			<td align="left">${comments}</td>
		</tr>
	</#if>
</table>
<br/><br/><br/>

 </td>
	</tr>
	<tr height="35px">
		<td align="left" style="color: black; background-color: #BFBFBF; font-size:12px; border-bottom-right-radius: 5px; border-bottom-left-radius: 5px">
<b>For help or more information, contact the Research Data Finder support group.</b><br/><br/>

Email: <a href="researchdatafinder@qut.edu.au">researchdatafinder@qut.edu.au</a> | Web: <a href="https://researchdatafinder.qut.edu.au/contact">https://researchdatafinder.qut.edu.au/contact</a>
		</td>
	</tr>
</table>
</body>
</html>