<#--
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
-->

<#-- Default VIVO individual profile page template (extends individual.ftl in vitro) -->

<#include "individual-setup.ftl">
<#import "lib-vivo-properties.ftl" as vp>

<#assign individualProductExtension>
    <#-- Include for any class specific template additions -->
    ${classSpecificExtension!}
    <@vp.webpages propertyGroups editable />
    <!--PREINDIVIDUAL OVERVIEW.FTL-->
    <#include "individual-overview.ftl">
        </section> <!-- #individual-info -->
    </section> <!-- #individual-intro -->
    <!--postindiviudal overiew tfl-->
</#assign>

<#include "individual-vitro.ftl">

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />', '<link rel="stylesheet" href="${urls.base}/edit/forms/qut_libs/messi/css/messi.min.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>')}

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/qut_libs/common/css/styles_help.css" />', '<link rel="stylesheet" href="${urls.base}/edit/forms/qut_libs/common/css/styles.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/edit/forms/qut_libs/ckeditor/js/ckeditor.js"></script>', '<script type="text/javascript" src="${urls.base}/edit/forms/qut_libs/messi/js/messi.min.js"></script>')}

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />', '<link rel="stylesheet" href="${urls.base}/css/individual/individual-display-style.css" />',
'<link rel="stylesheet" href="${urls.base}/edit/forms/qut_libs/tabs_navigation/css/tabs-navigation.css" />',
'<link rel="stylesheet" href="${urls.base}/edit/forms/qut_libs/growl/css/jquery.growl.css" />',
'<link rel="stylesheet" type="text/css" href="//researchdata.ands.org.au/apps/assets/orcid_widget/css/orcid_widget.css" />',
'<link rel="stylesheet" type="text/css" href="//researchdata.ands.org.au/apps/assets/location_capture_widget/css/location_capture_widget.css" />',
'<link rel="stylesheet" href="${urls.base}/edit/forms/qut_libs/common/css/jquery-ui.min.css">')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/edit/forms/qut_libs/tabs_navigation/js/tabs-navigation.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/edit/forms/qut_libs/growl/js/jquery.growl.js"></script>',
			  '<script type="text/javascript" src="//researchdata.ands.org.au/apps/assets/orcid_widget/js/orcid_widget.js"></script>',
			  '<script type="text/javascript" src="//maps.google.com/maps/api/js?sensor=false&libraries=drawing&v=3"></script>',
			  '<script type="text/javascript" src="//researchdata.ands.org.au/apps/assets/location_capture_widget/js/location_capture_widget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/edit/forms/qut_libs/common/js/jquery.simplemodal.1.4.2.min.js"></script>',
			  '<script src="${urls.base}/edit/forms/qut_libs/common/js/jquery-ui.min.js"></script>')}

			  
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',		  
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}	