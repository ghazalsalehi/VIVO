<%--
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.QUT_RESEARCH_DATA_PAGE.ACTION); %>
<vitro:confirmAuthorization />

<script type="text/javascript">
	$(function() {
		$('.rpt_section_head_row').click(function() {
			var sib = $(this).siblings('.rpt_section_sub_row');
			if (sib.is(':hidden')) {
				sib.slideDown(500);
				$(this).find('.rpt_section_exp_sign').html('-');
			} else {
				sib.slideUp(500);
				$(this).find('.rpt_section_exp_sign').html('+');
			}
		});
		$('.rpt_section_sub_row').hide();
	});
</script>
<style type="text/css">
.rpt_section {
	width: 930px;
	float: left;
	border: 1px solid #e0e0e0;
	margin-bottom: 1px;
}

.rpt_section_head_row {
	width: 910px;
	float: right;
	cursor: hand;
	cursor: pointer;
	padding: 5px 10px;
	background-color: #f0f0f0;
	/* color: #000000;  #83A849; */
	font-weight: bold;
}

.rpt_section_sub_row {
	width: 910px;
	float: right;
	padding: 5px 10px;
	margin-bottom: 1px;
}

.rpt_section_sub_row .rpt_section_lbl {
	margin-left: 25px;
}

.rpt_section_lbl {
	width: 220px;
	float: left;
}

.rpt_section_val {
	width: 300px;
	float: left;
}

.rpt_section_head_row .rpt_section_lbl {
	width: 245px;
}

.rpt_sub_evn_clr {
	background-color: #fafafa;
}

.rpt_sub_odd_clr {
	background-color: #f5f5f5;
}

.rpt_section_exp_sign {
	float: right;
	font-size: 17px;
}
</style>

<div class="staticPageBackground">

	<h2>Metadata Reports</h2>

	<div class="rpt_section">
		<div class="rpt_section_head_row">
			<div class='rpt_section_lbl'>People:</div>
			<div class='rpt_section_val'>${total_people}</div>
			<div class='rpt_section_exp_sign'>+</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Administrative Positions:</div>
			<div class='rpt_section_val'>${total_adminPositions}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_odd_clr">
			<div class='rpt_section_lbl'>Researchers:</div>
			<div class='rpt_section_val'>${total_researchers}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Groups:</div>
			<div class='rpt_section_val'>${total_groups}</div>
		</div>
	</div>
	
	<div class="rpt_section">
		<div class="rpt_section_head_row">
			<div class='rpt_section_lbl'>Activities:</div>
			<div class='rpt_section_val'>${total_activities}</div>
			<div class='rpt_section_exp_sign'>+</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Project:</div>
			<div class='rpt_section_val'>${total_projects}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_odd_clr">
			<div class='rpt_section_lbl'>Award:</div>
			<div class='rpt_section_val'>${total_awards}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Course:</div>
			<div class='rpt_section_val'>${total_courses}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Event:</div>
			<div class='rpt_section_val'>${total_events}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Program:</div>
			<div class='rpt_section_val'>${total_programs}</div>
		</div>
	</div>
	
	<div class="rpt_section">
		<div class="rpt_section_head_row">
			<div class='rpt_section_lbl'>Data Collections:</div>
			<div class='rpt_section_val'>${total_dataCollections}</div>
			<div class='rpt_section_exp_sign'>+</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Collection:</div>
			<div class='rpt_section_val'>${total_collections}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_odd_clr">
			<div class='rpt_section_lbl'>Research data set:</div>
			<div class='rpt_section_val'>${total_research_datasets}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Catalogue or index:</div>
			<div class='rpt_section_val'>${total_catalogueOrIndex}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Registry:</div>
			<div class='rpt_section_val'>${total_registry}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Repository:</div>
			<div class='rpt_section_val'>${total_repository}</div>
		</div>
	</div>
	
	<div class="rpt_section">
		<div class="rpt_section_head_row">
			<div class='rpt_section_lbl'>Services:</div>
			<div class='rpt_section_val'>${total_services}</div>
			<div class='rpt_section_exp_sign'>+</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Create:</div>
			<div class='rpt_section_val'>${total_create}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_odd_clr">
			<div class='rpt_section_lbl'>Generate:</div>
			<div class='rpt_section_val'>${total_generate}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Report:</div>
			<div class='rpt_section_val'>${total_report}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Annotate:</div>
			<div class='rpt_section_val'>${total_annotate}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Transform:</div>
			<div class='rpt_section_val'>${total_transform}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Assemble:</div>
			<div class='rpt_section_val'>${total_assemble}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Harvest OAI-PMH:</div>
			<div class='rpt_section_val'>${total_harvest_OAI-PMH}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Search HTTP:</div>
			<div class='rpt_section_val'>${total_search_HTTP}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Search open search:</div>
			<div class='rpt_section_val'>${total_searchOpenSearch}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Search SRU:</div>
			<div class='rpt_section_val'>${total_searchSRU}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Search SRW:</div>
			<div class='rpt_section_val'>${total_searchSRW}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Search Z39.50:</div>
			<div class='rpt_section_val'>${total_searchZ39_50}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Syndicate ATOM:</div>
			<div class='rpt_section_val'>${total_syndicateATOM}</div>
		</div>
		<div class="rpt_section_sub_row rpt_sub_evn_clr">
			<div class='rpt_section_lbl'>Syndicate RSS:</div>
			<div class='rpt_section_val'>${total_syndicateRSS}</div>
		</div>
	</div>

</div>
