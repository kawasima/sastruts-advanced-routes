sastruts-advanced-routes
========================

Routing filter and link tags like RoR for sastruts.

## Setup for routing

Prepare the following xml file.

	<routes>
		<root to="index#index"/>
		<match path="/user/list/:pageNo" controller="admin.User" action="index"/>
		<match path="/user/:id" controller="admin.User" action="show"/>
		<controller name="blog">
			<match path="post" to="post"/>
		</controller>
		<match path="/:controller/:action/:id"/>
	</routes>


