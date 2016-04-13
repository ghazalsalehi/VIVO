<#--
Copyright (c) 2014, QUT University
All rights reserved.

-->

<#-- Contact form email response -->

<#-- generate site map.  -->

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

	<#if errors??>
		<#list errors as error>
			<tr>
				<td align="left" style="font-weight:bold">${error}</td>
			</tr>
		</#list>
		
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