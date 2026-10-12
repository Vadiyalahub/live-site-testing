# Bug Reports

Defects found while testing the **Women in Tech Series 2026** landing page
(https://uninetworkgroup.com/women-in-tech-series-2026/).
First tested on **10 Oct 2026**, retested on **12 Oct 2026** after the page was redesigned for the next edition.

Every open bug has an automated test tagged `@known-bug` in
[`landing_page.feature`](../src/test/resources/features/wit/landing_page.feature).
Those tests fail until the bug is fixed. That's how we prove the fix later.

| ID | Title | Severity | Priority | Status (12 Oct) |
|----|-------|----------|----------|--------|
| BUG-008 | No way to register for the 15 Oct event | **High** | **High** | **Open (new)** |
| BUG-001 | Footer links About Us, Privacy Policy and Terms return 404 | High | High | Open |
| BUG-002 | Countdown shows "LIVE" forever after the event | Medium | High | Retest after 15 Oct |
| BUG-003 | FAQ still shows the old date (16 Sep) and length (75 min) | Medium | Medium | Open (changed) |
| BUG-004 | Images have no alt text (6, now 21) | Low | Medium | Open (worse) |
| BUG-005 | Meta description is only "Moderator & Panel" | Low | Low | **Fixed** |
| BUG-006 | Typo in a speaker interview URL ("womwn") | Low | Low | Open |
| BUG-007 | Speaker Leah (Beeler) Tate has no LinkedIn link | Low | Low | **Closed** |

**Severity** = how badly it hurts users. **Priority** = how soon it should be fixed.

---

## Retest after the redesign (12 Oct 2026)

The daily automated run failed on 12 Oct. The page had been redesigned for the next edition
(15 Oct 2026, Supply Chain & Logistics): new menu, new speakers, new date. I retested every bug:

- **Fixed:** BUG-005. The meta description is now a proper summary.
- **Closed:** BUG-007. That speaker is no longer listed, and all 8 new speakers have LinkedIn links.
- **Changed:** BUG-003. The agenda now says 90 minutes everywhere, but the FAQ still shows the old date and 75 minutes.
- **Worse:** BUG-004. All the new speaker photos have empty alt text, so it's 21 images now.
- **Can't confirm yet:** BUG-002. The countdown is counting to 15 Oct, so it's correct right now. The code
  that switches to "LIVE" with no end state is unchanged, so it'll come back after 15 Oct. The test checks this
  automatically from 16 Oct.
- **New:** BUG-008. The "Reserve Seat" button is gone.
- The tests for the registration form are now tagged `@blocked`. They can't run until BUG-008 is fixed,
  because there's no way to open the form.

---

## BUG-008: No way to register for the 15 Oct event

| | |
|---|---|
| **Severity** | High |
| **Priority** | High |
| **Found by** | Daily automated run (registration tests started failing), then confirmed by hand |
| **Environment** | Production, Chrome, desktop and mobile |

**Steps to reproduce**
1. Open the landing page.
2. Look for a "Reserve Seat" or "Register" button anywhere on the page.
3. Open the FAQ "How do I get the Zoom link?". It says *"Register on this page."*

**Expected:** A visible button that opens the registration form, as before the redesign.
**Actual:** There's no register button. The registration form is still in the page's HTML, but nothing opens it.
The only buttons are "Become a Partner", "Become a Speaker" and "Notify me" (for a later event).

**Why it matters:** The event is in 3 days and visitors have no way to sign up.

**Automated test:** `@BUG-008 Visitors can find a button to register`

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

**Retest 12 Oct:** the page now counts down to the next edition on 15 Oct, so the countdown is correct
for now. The script logic is unchanged, so the same thing will happen after 15 Oct. The test now checks
against 15 Oct and will fail from 16 Oct if it's still not fixed.

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

**Retest 12 Oct:** the hero and agenda now say 90 minutes everywhere. But the FAQ answer "When and where?"
still says *"Wednesday, 16 September 2026 · 10:00 AM PST · 75 minutes"*, the old edition's date and length.
The new event is 15 Oct, 90 minutes.

**Automated test:** `@BUG-003 FAQ shows the current event date and length`

---

## BUG-004: Images have no alt text

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

**Retest 12 Oct: worse, 21 images.** The redesign added 8 new speaker photos and 7 past-speaker photos,
all with `alt=""`. (Before the redesign, speaker photos had the speaker's name as alt text.)

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

**Retest 12 Oct: Fixed.** The description is now *"One platform. Diverse perspectives. A shared vision for the
future of technology."* The test passes and has moved to the regression suite.

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

**Retest 12 Oct: Closed.** The redesign replaced the speaker list, and all 8 new speakers have LinkedIn links.
The test passes and has moved to the regression suite, so a missing link will be caught next time.

---

## Other observations (not bugs)

- **7 forms are loaded on the page but only 1 is used.** Four WPForms, one Contact Form 7 and one
  Fluent Form are hidden. They slow the page down and give spam bots more targets.
- **Registrations are also saved in the visitor's browser** (`localStorage` key `witRegistrations`).
  Personal data stays on shared or public computers. Consider removing this.
