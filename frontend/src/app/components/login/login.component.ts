/**
 * ログインコンポーネント
 *
 * 消防司令システムのユーザーログイン機能を提供します。
 * ユーザー名とパスワードによる認証、エラーハンドリング、
 * ログイン成功後のリダイレクト処理を担当します。
 *
 * @author FireCaptain Team
 * @version 1.0
 */

import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  /**
   * ログイン処理
   */
  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const loginRequest: LoginRequest = {
        username: this.loginForm.value.username,
        password: this.loginForm.value.password
      };

      this.authService.login(loginRequest).subscribe({
        next: (response) => {
          this.authService.setUser(response.user, response.token);
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'ログインに失敗しました。';
        }
      });
    }
  }

  /**
   * フォームのエラー状態を取得
   *
   * @param fieldName フィールド名
   * @returns エラーメッセージ
   */
  getErrorMessage(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName === 'username' ? 'ユーザー名' : 'パスワード'}は必須です。`;
    }
    if (field?.hasError('minlength')) {
      const minLength = fieldName === 'username' ? 3 : 6;
      return `${fieldName === 'username' ? 'ユーザー名' : 'パスワード'}は${minLength}文字以上で入力してください。`;
    }
    return '';
  }
}
