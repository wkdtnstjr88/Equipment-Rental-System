## 💻 備品貸出管理システム (Equipment Rental System)
実際の稼働状況を確認する: [公式サイトへ] https://port-0-equipment-rental-system-mn0t70n4b7e2de36.sel3.cloudtype.app/

テスト用アカウント
ID: user1
Password: user123!@#

### 1. プロジェクト概要
本プロジェクトは、効率的な備品管理と貸出プロセスの自動化を目的としたバックエンドシステムです。単なる機能実装にとどまらず、**データ整合性のためのバリデーション**、**共通例外処理**、およびそれらを保証する**テストコードの作成**に重点を置いて開発しました。

---

### 2. 主要機能 (Key Features)

**① 会員管理および認証 (Auth & Member)**
・会員登録およびログイン: HTTPセッションの概念を適用した認証セッション管理の実装
・入力値バリデーション: @Validを活用し、パスワードの複雑性や必須項目に対するサーバーサイド検証を徹底
・個人情報修正: ログイン状態に応じた会員情報の更新機能

![Member Management](https://github.com/user-attachments/assets/6c4e2434-7769-4257-b337-f1fea4a51f57)

**② 備品貸出管理 (Rental Business Logic)**
・貸出制限ロジック: 過去日付の予約禁止および最大貸出期間（7日間）の制限を実装し、ビジネスルールの整合性を確保
・ステータス管理: 備品の貸出状態（AVAILABLE/RENTED）をリアルタイムで反映し、二重貸出を防止
・貸出履歴の高度化: ページング処理、検索、フィルタリング機能を実装し、大量のデータでもスムーズな照会が可能

![Rental Management](https://github.com/user-attachments/assets/ac23d422-1a74-4dbd-a055-d478ca6f9e3a)

---

### 3. トラブルシューティング (Troubleshooting)

**① 日時（DateTime）形式の不一致による検索エラー**
・問題点: ユーザーが入力する日付（Date）とDBに保存された日時（DateTime）の形式が異なり、検索結果が出ないバグが発生
・解決策: JPQLで CAST 関数を使用し、比較時のみ日付形式に変換。時分秒に関わらず該当日の全データを正確に抽出するよう改善

**② 初期遷移時のデータ非表示現象の解決**
・原因: keyword パラメータのデフォルト値がないため、初期遷移時に null が渡され照会が実行されない問題
・解決策: @RequestParam に defaultValue = "" を設定し、検索ワードがない初期状態でも全リストが自動的にロードされるよう改善

**③ 例外発生時のホワイトラベルエラーページ露出問題**
・現象: 重複IDの登録など、ビジネスロジック例外の発生時に500内部サーバーエラーページが露出
・解決策: コントローラー内に例外処理ハンドラー（try-catch）を実装し、例外発生時に適切なエラーメッセージと共に登録フォームへリダイレクト

---

### 4. レイヤー別テスト戦略 (Testing Strategy)

**ユニットテスト (Unit Test)**
Mockitoを活用してDB依存を排除し、「過去日付の予約禁止」などの核心的なビジネスロジックを高速に検証

**結合テスト (Integration Test)**
実際のSpringコンテナ上でセッション、リダイレクト、DB反映までの全ライフサイクルを検証し、システムの信頼性を向上

---

### 5. 技術スタック (Tech Stack)
Backend: Java 17, Spring Boot 3.x, Spring Data JPA
Database: MySQL (H2 for testing)
Testing: JUnit 5, Mockito, MockMvc, AssertJ
Frontend: Thymeleaf, Bootstrap 5

---

### 6. テスト結果 (Verification)
・テスト成功率 100%: 全てのビジネスロジックおよびAPIエンドポイントに対するテストをパス
・自動ロールバック: @Transactional を適用し、テスト実行によるDBデータの汚染を完全に防止