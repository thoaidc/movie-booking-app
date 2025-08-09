import { Injectable, OnDestroy } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { filter, first, switchMap } from 'rxjs/operators';
import {environment} from '../../../environments/environment';

// Connection state enum for easy management
export enum SocketClientState {
  ATTEMPTING = 0,
  CONNECTED = 1,
  DISCONNECTED = 2
}

@Injectable({
  providedIn: 'root'
})
export class WebsocketService implements OnDestroy {
  private client: Client;
  private state: BehaviorSubject<SocketClientState>;
  private topicSubscriptions: Map<string, StompSubscription> = new Map();
  private SOCKET_SERVER_URL = `${environment.SERVER_API_URL}ws-booking`;

  constructor() {
    this.client = new Client({
      brokerURL: this.SOCKET_SERVER_URL,
      // Auto reconnect after 5 seconds if connection is lost
      reconnectDelay: 5000,
      // Heartbeat to keep connection (milliseconds)
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => this.state.next(SocketClientState.CONNECTED),
      onStompError: () => this.state.next(SocketClientState.DISCONNECTED),
      onWebSocketClose: () => {
        this.state.next(SocketClientState.DISCONNECTED);
        this.clearSubscriptions(); // Delete subscriptions when websocket closes
      },
      onWebSocketError: () => this.state.next(SocketClientState.DISCONNECTED)
    });

    // Initialize BehaviorSubject with initial state DISCONNECTED
    this.state = new BehaviorSubject<SocketClientState>(SocketClientState.DISCONNECTED);
  }

  // Observable to track connection status
  onState(): Observable<SocketClientState> {
    return this.state.asObservable();
  }

  // Function to connect to WebSocket server
  connect(): void {
    if (this.state.value === SocketClientState.CONNECTED || this.state.value === SocketClientState.ATTEMPTING) {
      return;
    }

    this.state.next(SocketClientState.ATTEMPTING);
    this.client.activate();
  }

  disconnect(): void {
    if (this.client.active) {
      this.client.deactivate().then(() => {
        this.state.next(SocketClientState.DISCONNECTED);
        this.clearSubscriptions(); // Delete subscriptions when actively disconnected
      }).catch(() => {
        this.state.next(SocketClientState.DISCONNECTED);
        this.clearSubscriptions();
      });
    } else {
      // Make sure the status is disconnected if not active
      if (this.state.value !== SocketClientState.DISCONNECTED) {
        this.state.next(SocketClientState.DISCONNECTED);
        this.clearSubscriptions();
      }
    }
  }

  // Subscribe to a topic and return an Observable so that the component can receive messages from this topic
  subscribeToTopic(topic: string): Observable<IMessage> {
    // Wait until the client connects successfully
    return this.state.pipe(
      filter(state => state === SocketClientState.CONNECTED), // Only continue when CONNECTED
      first(), // Only get the first CONNECTED after calling this function
      switchMap(() => {
        // Create a new Subject for this topic
        const topicSubject = new Subject<IMessage>();
        const subscription = this.client.subscribe(topic, (message: IMessage) => {
          // Push received messages to the topic's Subject
          topicSubject.next(message);
        });

        // Save subscription for management (e.g. unsubscribe later)
        this.topicSubscriptions.set(topic, subscription);
        return topicSubject.asObservable();
      })
    );
  }

  unsubscribeFromTopic(topic: string): void {
    const subscription = this.topicSubscriptions.get(topic);

    if (subscription) {
      subscription.unsubscribe();
      this.topicSubscriptions.delete(topic);
    }
  }

  // Function to send message to server (STOMP)
  // Destination must start with prefix configured in backend (`/api/ws/`)
  // noinspection JSUnusedGlobalSymbols
  sendMessage(destination: string, body: any): void {
    if (this.client.active) {
      this.client.publish({
        destination: destination,
        body: JSON.stringify(body) // Convert content to JSON
      });
    }
  }

  private clearSubscriptions(): void {
    this.topicSubscriptions.clear();
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
