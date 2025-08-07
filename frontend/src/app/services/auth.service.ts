/**
 * 認証サービス
 *
 * 消防司令システムのユーザー認証機能を提供します。
 * ログイン、ログアウト、認証状態の管理、JWTトークンの処理などを担当します。
 *
 * @author FireCaptain Team
 * @version 1.0
 */

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

/**
 * ユーザー情報のインターフェース
 */
export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  firstName?: string;
  lastName?: string;
}

/**
 * ログイン要求のインターフェース
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * ログイン応答のインターフェース
 */
export interface LoginResponse {
  token: string;
  user: User;
  expiresIn: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  /**
   * ユーザーログイン
   *
   * @param credentials ログイン認証情報
   * @returns ログイン応答のObservable
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, credentials);
  }

  /**
   * ユーザーログアウト
   */
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  /**
   * 現在のユーザーを取得
   *
   * @returns 現在のユーザー情報
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * 認証状態を取得
   *
   * @returns 認証されているかどうか
   */
  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  /**
   * JWTトークンを取得
   *
   * @returns 保存されているJWTトークン
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * ユーザー情報を保存
   *
   * @param user ユーザー情報
   * @param token JWTトークン
   */
  setUser(user: User, token: string): void {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
  }

  /**
   * 保存されたユーザー情報を読み込み
   */
  private loadStoredUser(): void {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
      } catch (error) {
        console.error('保存されたユーザー情報の解析に失敗しました:', error);
        this.logout();
      }
    }
  }

  /**
   * トークンの有効性を確認
   *
   * @returns トークンが有効かどうか
   */
  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationDate = new Date(payload.exp * 1000);
      return expirationDate > new Date();
    } catch (error) {
      return false;
    }
  }
}
