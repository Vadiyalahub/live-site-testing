# Bug Reports

Defects found while testing the **Women in Tech Series 2026** landing page
(https://uninetworkgroup.com/women-in-tech-series-2026/) on **10 Oct 2026**.

Every bug has an automated test tagged `@known-bug` in
[`landing_page.feature`](../src/test/resources/features/wit/landing_page.feature).
Those tests fail until the bug is fixed. That's how we prove the fix later.

| ID | Title | Severity | Priority | Status |
|----|-------|----------|----------|--------|
| BUG-001 | Footer links About Us, Privacy Policy and Terms return 404 | High | High | Open |
| BUG-002 | Countdown still shows "LIVE" weeks after the event ended | Medium | High | Open |
| BUG-003 | Session length shown as both 75 and 90 minutes | Medium | Medium | Open |
| BUG-004 | 6 images have no alt text | Low | Medium | Open |
| BUG-005 | Meta description is only "Moderator & Panel" | Low | Low | Open |
| BUG-006 | Typo in a speaker interview URL ("womwn") | Low | Low | Open |
| BUG-007 | Speaker Leah (Beeler) Tate has no LinkedIn link | Low | Low | Open |

**Severity** = how badly it hurts users. **Priority** = how soon it should be fixed.

---

## BUG-001: Footer links return 404 (page not found)

| | |
|---|---|
| **Severity** | High |
| **Priority** | High |
| **Found by** | Automated link check (`LinkChecker`) |
| **Environment** | Production, Chrome 1920×1080 and HTTP requests |

**Steps to reproduce**
1. Open the landing page.
2. Scroll to the footer.
3. Click **About Us**, **Privacy Policy** or **Terms & Conditions**.

**Expected:** Each link opens its page.
**Actual:** All three return **HTTP 404**:
- `https://uninetworkgroup.com/page/about-us`
- `https://uninetworkgroup.com/page/privacy-policy`
- `https://uninetworkgroup.com/page/terms-condition`

**Why it matters:** The page collects names and emails in its registration form.
A working privacy policy link is expected (and in many regions legally required).

**Automated test:** `@BUG-001 Links to other pages on the site are not broken`

---

## BUG-002: Countdown still shows "LIVE" after the event ended

| | |
|---|---|
| **Severity** | Medium |
| **Priority** | High |
| **Found by** | Exploratory testing, then automated |

**Steps to reproduce**
1. Open the landing page any time after 16 Sep 2026.
2. Look at the "Countdown to the roundtable" block.

**Expected:** After the event, show something like "This session has ended. Watch the recording."
**Actual:** Shows `00 Days 00 Hours LIVE` permanently. Visitors think the event is happening now,
and the "Reserve Seat" button still accepts registrations for an event in the past.

**Root cause (from page source):** the countdown script sets "LIVE" whenever
`EVENT_DATE - now <= 0`, with no end time.

**Automated test:** `@BUG-002 Countdown stops saying LIVE after the event`

---

## BUG-003: Session length shown as both 75 and 90 minutes

| | |
|---|---|
| **Severity** | Medium |
| **Priority** | Medium |
| **Found by** | Content review, then automated |

**Steps to reproduce**
1. Read the hero section and FAQ: they say **75 mins** / **75-minute**.
2. Scroll to the agenda section: the heading says **"The 90-minute flow"** and a label says **"90 Minutes"**.

**Expected:** One consistent session length.
**Actual:** Conflicting information confuses attendees planning their time.

**Automated test:** `@BUG-003 Session length is the same everywhere on the page`

---

## BUG-004: 6 images have no alt text

| | |
|---|---|
| **Severity** | Low |
| **Priority** | Medium |
| **Found by** | Automated accessibility check |

**Actual:** These images have an empty or missing `alt` attribute:
- 2 × `Transparent-UNI-logo.png` (site logo)
- 3 × `maxresdefault.jpg` (YouTube video thumbnails in "Sessions worth watching")
- 1 × `image-1788893886189.webp`

**Expected:** Every meaningful image has alt text so screen-reader users know what it shows
(WCAG 2.1 rule 1.1.1).

**Automated test:** `@BUG-004 Every image has alt text for screen readers`

---

## BUG-005: Meta description is only "Moderator & Panel"

| | |
|---|---|
| **Severity** | Low |
| **Priority** | Low |
| **Found by** | Automated SEO check |

**Actual:** `<meta name="description" content="Moderator & Panel">`. Google shows this
under the page title in search results, so it tells people nothing about the event.

**Expected:** A 120–160 character summary, e.g. *"Free virtual roundtable: 7–8 senior women
leaders in manufacturing and industrial automation. 75 minutes live on Zoom."*

**Automated test:** `@BUG-005 Page has a useful meta description for search engines`

---

## BUG-006: Typo in a speaker interview URL

| | |
|---|---|
| **Severity** | Low |
| **Priority** | Low |
| **Found by** | Manual review of the link list from `LinkChecker` |

**Actual:** `https://uninetworkgroup.com/interview-womwn-in-tech-lakshmi-devi/` ("womwn").
The page works, but the URL looks unprofessional when shared.

**Fix:** Rename the slug to `interview-women-in-tech-lakshmi-devi` and add a 301 redirect from the old one.

---

## BUG-007: Speaker Leah (Beeler) Tate has no LinkedIn link

| | |
|---|---|
| **Severity** | Low |
| **Priority** | Low |
| **Found by** | Automated check of every speaker card |

**Steps to reproduce**
1. Scroll to "Opening the conversation" (the speakers section).
2. Look at the LinkedIn icon on each of the 6 speaker cards.

**Expected:** Every speaker card has a LinkedIn link, like the other 5.
**Actual:** Leah (Beeler) Tate's card has no LinkedIn link.

**Automated test:** `@BUG-007 Every speaker card links to the speaker's LinkedIn`

---

## Other observations (not bugs)

- **7 forms are loaded on the page but only 1 is used.** Four WPForms, one Contact Form 7 and one
  Fluent Form are hidden. They slow the page down and give spam bots more targets.
- **Registrations are also saved in the visitor's browser** (`localStorage` key `witRegistrations`).
  Personal data stays on shared or public computers. Consider removing this.
