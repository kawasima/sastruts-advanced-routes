SAStruts Advanced Routes
========================

SAStrutsでRuby on Railsのようなルーティングを実現するためのライブラリです。

## ルーティングの設定

以下のようなルーティング用のXMLを用意します。

	<routes>
		<match path="/user/list" controller="admin.User" action="index"/>
		<match path="/user/:id" controller="admin.User" action="show"/>
		<controller name="blog">
			<match path="post" to="post"/>
		</controller>
	</routes>

match は、基本のルーティング設定で、pathは

コントローラごとに設定を書くのは、

## URLを作る

リンクをJSPなどのViewで出力する場合、urlForのヘルパーメソッドを使って以下のように指定できます。

${ar:urlFor(ar:options().$("action", "new").$("id", 1))}

