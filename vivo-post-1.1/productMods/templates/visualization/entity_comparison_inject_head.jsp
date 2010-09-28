<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir"><c:out value="${portalBean.themeDir}" /></c:set>
<c:set var="contextPath"><c:out value="${pageContext.request.contextPath}" /></c:set>

<c:url var="jquery" value="/js/jquery.js"/>
<c:url var="flot" value="/js/jquery_plugins/flot/jquery.flot.js"/>
<c:url var="fliptext" value="/js/jquery_plugins/fliptext/jquery.mb.flipText.js"/>
<c:url var="jgrowl" value="/js/jquery_plugins/jgrowl/jquery.jgrowl.js"/>
<c:url var="pagination" value="/js/jquery_plugins/pagination/jquery.pagination.js"/>

<c:url var="entityComparisonUtils" value="/js/visualization/entityComparison/util.js" />
<c:url var="entityComparisonConstants" value="/js/visualization/entityComparison/constants.js" />

<!-- css related to jgrowl and pagination js files. -->
<c:url var="paginationStyle" value="/js/jquery_plugins/pagination/pagination.css" />
<c:url var="jgrowlStyle" value="/js/jquery_plugins/jgrowl/jquery.jgrowl.css" />

<c:url var="entityComparisonStyle" value="/${themeDir}css/visualization/entityComparison/layout.css" />
<c:url var="vizStyle" value="/${themeDir}css/visualization/visualization.css" />

<!-- Including jquery, entity comparison related javascript files -->

<script type="text/javascript" src="${jquery}"></script>
<script type="text/javascript" src="${flot}"></script>
<script type="text/javascript" src="${fliptext}"></script>
<script type="text/javascript" src="${jgrowl}"></script>
<script type="text/javascript" src="${pagination}"></script>
<script type="text/javascript" src="${entityComparisonUtils}"></script>
<script type="text/javascript" src="${entityComparisonConstants}"></script>

<link href="${entityComparisonStyle}" rel="stylesheet" type="text/css" />
<link href="${paginationStyle}" rel="stylesheet" type="text/css" />
<link href="${jgrowlStyle}" rel="stylesheet" type="text/css" />

<link rel="stylesheet" type="text/css" href="${vizStyle}" />