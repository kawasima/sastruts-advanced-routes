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

コントローラ(sastrutsでいうところのAction)は、アクションパッケージ以降の名前で書きます。サフィックスのActionは不要です。
すなわち、com.exampleがルートパッケージの場合は、admin.Userと書くとcom.example.action.admin.UserActionのアクションクラスを
指すことになります。

match は基本のルーティング設定で、path属性にマッチすると、そのコントローラ、アクションにforwardされます。
controllerタグでコントローラごとの設定をまとめることができます。


### Namespace

ルーティングをグループ化するために、namespaceを使うことができます。

	<namespace name="admin">
		<match path="posts" to="Posts#index"/>
	</namespace>

と書くと、/admin/posts のパスが admin.PostsAction にマッチするようになります。

### 特別なパラメータ

:controller と :action は特別なパラメータで、:controllerはアプリケーション内のActionクラスにマッピングされ、
:actionはActionクラスのメソッドにマッピングされます。
したがって、デフォルトのSAStrutsのルーティングは以下の1行で表現されます。

	<match path=":controller/:action" />

### Dynamic Segments

任意のパラメータをURLに付け加えることができます。

	<match path=":controller/:action/:id/:userId"/>

というルーティングでは、例えば /photos/show/1/2 というリクエストが来ると、PhotosActionのshowメソッドに転送され、
Actionのプロパティ、またはActionFormのid属性に"1"が、userId属性に"2"が設定されます。

namespaceの中で:controllerセグメントを使うことはできません。そうする必要があれば、:controllerを

### Static Segments

固定のパスを途中に挟み込むことができます。

	<match path=":controller/:action/:id/with_user/:userId"/>

というルーティングは、 /photos/show/1/with_user/2 のようなURLに対応し、

	{ :controller => "Photos", :action => "show", :id => "1", userId => "2" }

というパラメータ構成になります。

### Query String

当然ながら任意のパラメータをクエリ文字列から取得することもできます。

	<match path=":controller/:action/:id"/>

というルーティングは、 /photos/show/1?userId=2 のようなURLに対応し、

	{ :controller => "Photos", :action => "show", :id => "1", userId => "2" }

というパラメータ構成になります。

### デフォルト

未実装です。

### HTTPメソッド

HTTPメソッドによってルーティングを分けたい場合には

	<get  path="photos" to="Photos#list"/>
	<post path="photos" to="Photos#update"/>

のように書いておくと、/photos へGETでアクセスすると Photos#list へ、POSTでアクセスすると Photos#update へ転送されます。

### グロブ

	<match path="photos/*other" to="Photos#unknown"/>

というルーティングは、photos/12 や /photos/long/path/to/12 というパスにマッチし、otherパラメータに
"12" または "long/path/to/12" がセットされます。

ワイルドカードはルートのどこにあっても構いません。

	<match path="books/*section/:title" to="Books#show"/>

は、books/some/section/last-words-a-memoir のパスにマッチし、sectionパラメータに"some/section"が、
titleパラメータに"last-words-a-memoir" がセットされます。

## URLを作る

リンクをJSPなどのViewで出力する場合、urlForのヘルパーメソッドを使って以下のように指定できます。

	${ar:urlFor("admin.User#index?pageNo=2")}

こう書いておくと、ルーティング設定にしたがって、

	/user/list/2

というURLを出力してくれます。


## AdvancedRoutingFilter

以上の設定をしたうえで、sastrutsのRoutingFilterの代わりにAdvancedRoutingFilterを使うと、上記ルーティング定義に
したがって、リクエストをアクションメソッドに転送してくれるようになります。

web.xmlにて、次のようにroutingfilterを設定されているところを、

	<filter>
		<filter-name>routingfilter</filter-name>
		<filter-class>org.seasar.struts.filter.RoutingFilter</filter-class>
		<init-param>
			<param-name>jspDirectAccess</param-name>
			<param-value>false</param-value>
		</init-param>
	</filter>

次のようにAdvancedRoutingFilterを使うように書き換えてください。

	<filter>
		<filter-name>routingfilter</filter-name>
		<filter-class>net.unit8.sastruts.AdvancedRoutingFilter</filter-class>
		<init-param>
			<param-name>jspDirectAccess</param-name>
			<param-value>false</param-value>
		</init-param>
		<init-param>
			<param-name>jspDirectAccess</param-name>
			<param-value>false</param-value>
		</init-param>
		<init-param>
			<param-name>routes</param-name>
			<param-value>/WEB-INF/routes.xml</param-value>
		</init-param>
		<init-param>
			<param-name>checkInterval</param-name>
			<param-value>-1</param-value>
		</init-param>
	</filter>

追加されているパラメータについて説明します。

routes は、ルート定義ファイルのパスを設定します。webapp以下からのパスで記述してください。

checkInterval は、ルート定義ファイルの更新をチェックしにいく間隔(秒数)を設定します。この間隔でチェックし更新があれば
定義ファイルをリロードします。0を設定すると常に更新チェックするようになりますが、これは負荷が高いため本番環境では
避けるようにしてください。このパラメータを設定しない、またはマイナスの値を設定すると更新チェック自体がおこなわれず、
アプリケーションを再起動しない限りルート定義はリロードされません。

## TODO

* デフォルトの設定を実装する。

## License

SAStruts Advanced Routes はApache License 2.0 の元に配布されます。

* http://www.apache.org/licenses/LICENSE-2.0.txt

