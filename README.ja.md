SAStruts Advanced Routes
========================

SAStrutsでRuby on Railsのようなルーティングを実現するためのライブラリです。

## ルーティングの設定

以下のようなルーティング用のXMLを用意します。

	<routes>
		<root to="index#index"/>
		<match path="/user/list/:pageNo" controller="admin.User" action="index"/>
		<match path="/user/:id" controller="admin.User" action="show"/>
		<controller name="blog">
			<match path="post" to="post"/>
		</controller>
		<match path="/:controller/:action/:id"/>
	</routes>

match は基本のルーティング設定で、path属性にマッチすると、そのコントローラ、アクションにforwardされます。
controllerタグでコントローラごとの設定をまとめることができます。

## URLを作る

リンクをJSPなどのViewで出力する場合、urlForのヘルパーメソッドを使って以下のように指定できます。

	${ar:urlFor("admin.User#index?pageNo=2")}

こう書いておくと、ルーティング設定にしたがって、

	/user/list/2

というURLを出力してくれます。

## TODO

* HotDeploy環境で動かない
* マッチングの最適化を実装する

